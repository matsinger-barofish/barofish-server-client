package com.matsinger.barofishserver.deliver.application;

import com.matsinger.barofishserver.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.admin.log.domain.AdminLogType;
import com.matsinger.barofishserver.deliver.domain.DeliveryCompany;
import com.matsinger.barofishserver.deliver.repository.DeliveryCompanyRepository;
import com.matsinger.barofishserver.deliver.domain.Deliver;
import com.matsinger.barofishserver.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.order.application.OrderService;
import com.matsinger.barofishserver.order.domain.Orders;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.user.dto.UserJoinReq;
import com.matsinger.barofishserver.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
    private final DeliverPlaceRepository deliverPlaceRepository;
    private final Common util;
    private final DeliveryCompanyRepository deliveryCompanyRepository;
    private final AdminLogQueryService adminLogQueryService;
    private final AdminLogCommandService adminLogCommandService;
    private final Common utils;
    private final NotificationCommandService notificationCommandService;

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
                info.setDeliveryDoneAt(util.now());
                orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));

                Product product = productService.selectProduct(info.getProductId());

                Orders findOrder = orderService.selectOrder(info.getOrderId());
                notificationCommandService.sendFcmToUser(
                        findOrder.getUserId(),
                        NotificationMessageType.DELIVER_DONE,
                        NotificationMessage
                                .builder().productName(product.getTitle())
                                .build());

                String content = product.getTitle() + " 주문이 배송 완료 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(1).type(AdminLogType.ORDER).targetId(
                                info.getOrderId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
        }
    }

    public DeliverPlace createAndSaveDeliverPlace(User user, UserInfo userInfo, UserJoinReq request) throws Exception {

        String address = util.validateString(request.getAddress(), 100L, "주소");
        String addressDetail = util.validateString(request.getAddressDetail(), 100L, "상세 주소");

        if (request.getPostalCode() == null) {
            throw new IllegalArgumentException("우편 번호를 입력해주세요.");
        }

        DeliverPlace
                createdDeliver =
                DeliverPlace.builder().userId(user.getId()).name(userInfo.getName()).receiverName(userInfo.getName()).tel(
                        userInfo.getPhone()).address(address).addressDetail(addressDetail).deliverMessage("").postalCode(
                        request.getPostalCode()).isDefault(true).build();

        return deliverPlaceRepository.save(createdDeliver);
    }
}
