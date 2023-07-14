package com.matsinger.barofishserver.deliver;

import com.matsinger.barofishserver.order.OrderService;
import com.matsinger.barofishserver.order.object.OrderProductInfo;
import com.matsinger.barofishserver.order.object.OrderProductState;
import com.matsinger.barofishserver.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeliverService {
    String SWEET_TRACKER_BASE_URL = "http://info.sweettracker.co.kr";
    @Value("${smart-parcel.apiKey}")
    private String accessKey;
    String COMPANY_LIST_URL = SWEET_TRACKER_BASE_URL + "/api/v1/companylist";
    String RECOMMEND_COMPANY_LIST_URL = SWEET_TRACKER_BASE_URL + "/api/v1/recommend";
    String TRACKING_INFO_URL = SWEET_TRACKER_BASE_URL + "/api/v1/trackingInfo";

    private final ProductService productService;
    private final OrderService orderService;
    private final DeliveryCompanyRepository deliveryCompanyRepository;

    public List<Deliver.Company> selectDeliverCompanyList() {
        List<DeliveryCompany> deliveryCompanies = deliveryCompanyRepository.findAll();
        return deliveryCompanies.stream().map(v -> Deliver.Company.builder().Name(v.getName()).Code(v.getCode()).International(
                null).build()).toList();
    }

    public DeliveryCompany selectDeliveryCompanyWithCode(String code) {
        return deliveryCompanyRepository.findById(code).orElseThrow(() -> {
            throw new Error("유효하지 않은 택배사 코드입니다.");
        });
    }

    public List<Deliver.Company> selectRecommendDeliverCompanyList(String invoice) {
        RestTemplate restTemplate = new RestTemplate();
        String url = RECOMMEND_COMPANY_LIST_URL + "?t_key=" + accessKey + "&t_invoice=" + invoice;
        String jsonString = restTemplate.getForObject(url, String.class);
        JSONObject object = new JSONObject(jsonString);
        JSONArray arrObject = object.getJSONArray("Recommend");
        List<Deliver.Company> companies = new ArrayList<>();
        for (int i = 0; i < arrObject.length(); i++) {
            JSONObject obj = arrObject.getJSONObject(i);
            companies.add(Deliver.Company.builder().Code(obj.getString("Code")).Name(obj.getString("Name")).build());
        }
        return companies;
    }

    public Deliver.TrackingInfo selectTrackingInfo(String code, String invoice) {
        try {


            RestTemplate restTemplate = new RestTemplate();
            String url = TRACKING_INFO_URL + "?t_key=" + accessKey + "&t_invoice=" + invoice + "&t_code=" + code;
            String jsonString = restTemplate.getForObject(url, String.class);
            JSONObject object = new JSONObject(jsonString);
//        JSONObject object = object.getJSONObject("tracking_info");
            try {
                Boolean status = object.getBoolean("status");
//                if (!status) throw new Error("유효하지 않은 운송장 번호이거나 택배사 코드입니다.");
                return null;
            } catch (Exception e) {}
            try {
                JSONArray trackingDetailList = object.getJSONArray("trackingDetails");
                Deliver.TrackingInfo
                        trackingInfo =
                        Deliver.TrackingInfo.builder().adUrl(object.getString("adUrl")).invoiceNo(object.getString(
                                "invoiceNo")).itemImage(object.getString("itemImage")).itemName(object.getString(
                                "itemName")).level(object.getInt("level")).result(object.getString("result")).senderName(
                                object.getString("senderName")).build();
                List<Deliver.TrackingDetails> trackingDetails = new ArrayList<>();
                for (int i = 0; i < trackingDetailList.length(); i++) {
                    JSONObject obj = trackingDetailList.getJSONObject(i);
                    trackingDetails.add(Deliver.TrackingDetails.builder().code(String.valueOf(obj.get("code"))).kind(
                            String.valueOf(obj.get("kind"))).level(obj.getInt("level")).manName(obj.getString("manName")).manPic(
                            obj.getString("manPic")).timeString(obj.getString("timeString")).where(obj.getString("where")).build());
                }
                trackingInfo.setTrackingDetails(trackingDetails);
                return trackingInfo;
            } catch (Exception e) {
                return null;
//                throw new Error("유효하지 않은 운송장 번호이거나 택배사 코드입니다.");
            }
        } catch (Error e) {
            return null;
        }
    }

    public void refreshOrderDeliverState() {
        List<OrderProductInfo>
                infos =
                orderService.selectOrderProductInfoWithState(new ArrayList<>(List.of(OrderProductState.ON_DELIVERY)));
        infos = infos.stream().filter(v -> v.getInvoiceCode() != null).toList();
        for (OrderProductInfo info : infos) {
            Deliver.TrackingInfo trackingInfo = selectTrackingInfo(info.getDeliverCompanyCode(), info.getInvoiceCode());
            if (trackingInfo != null && trackingInfo.getLevel() == 6) {
                info.setState(OrderProductState.DELIVERY_DONE);
                orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            }
        }
    }
}
