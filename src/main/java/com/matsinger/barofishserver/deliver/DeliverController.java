package com.matsinger.barofishserver.deliver;

import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliver")
public class DeliverController {

    private final DeliverService deliverService;

    @GetMapping("/company/list")
    public ResponseEntity<CustomResponse<List<Deliver.Company>>> selectDeliverCompanyList() {
        CustomResponse<List<Deliver.Company>> res = new CustomResponse<>();
        try {

            res.setData(Optional.ofNullable(deliverService.selectDeliverCompanyList()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/company/recommend")
    public ResponseEntity<CustomResponse<List<Deliver.Company>>> selectRecommendDeliverCompanyList(@RequestParam("invoice") String invoice) {
        CustomResponse<List<Deliver.Company>> res = new CustomResponse<>();
        try {
            res.setData(Optional.ofNullable(deliverService.selectRecommendDeliverCompanyList(invoice)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/tracking")
    public ResponseEntity<CustomResponse<Deliver.TrackingInfo>> selectTrackingInfo(@RequestParam("invoice") String invoice,
                                                                                   @RequestParam("code") String code) {
        CustomResponse<Deliver.TrackingInfo> res = new CustomResponse<>();
        try {
            res.setData(Optional.ofNullable(deliverService.selectTrackingInfo(code, invoice)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
