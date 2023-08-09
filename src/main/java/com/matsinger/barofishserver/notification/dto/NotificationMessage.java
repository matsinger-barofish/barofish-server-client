package com.matsinger.barofishserver.notification.dto;

import com.matsinger.barofishserver.notification.domain.NotificationType;
import lombok.*;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class NotificationMessage {
    //    private NotificationMessageType type;
    private String productName;
    private Timestamp orderedAt;
    private String userName;
    private String couponName;
    private String customContent;
    private Boolean isCanceledByRegion;

    private String convertTimestamp2Str(Timestamp time) {
        return "";
    }

    public String getMessage(NotificationMessageType type) {
        return switch (type) {
            case PAYMENT_DONE -> String.format("<strong>%s</strong> 상품의 결제가 완료되었습니다.", this.productName);
            case DELIVER_READY -> String.format("주문하신 <strong>%s</strong> 상품의 배송 준비가 완료되었습니다.", this.productName);
            case DELIVER_START ->
                    String.format("주문하신 <strong>%s</strong> 상품의 배송이 시작되었습니다. 빠르고 신선하게 배송해드릴게요 :)", this.productName);
            case DELIVER_DONE ->
                    String.format("주문하신 <strong>%s</strong> 상품의 배송을 완료하였습니다. 이용해주셔서 감사합니다.", this.productName);
            case ORDER_CANCEL ->
                    this.isCanceledByRegion ? String.format("주문하신 <string>%s</strong> 상품이 주문 불가 지역이라 자동으로 " +
                            "주문이 취소되었습니다.", this.productName) : String.format("주문하신 <strong>%s</strong> 상품이 취소되었습니다.",
                            this.productName);
            case CANCEL_REJECT -> String.format("신청하신 <strong>%s</strong> 상품 취소건이 반려되었습니다.", this.productName);
            case EXCHANGE_REJECT -> String.format("신청하신 <strong>%s</strong> 상품 교환건이 반려되었습니다.", this.productName);
            case EXCHANGE_ACCEPT -> String.format("신청하신 <strong>%s</strong> 상품 교환건이 접수되었습니다.", this.productName);
            case REFUND_REJECT -> String.format("신청하신 <strong>%s</strong> 상품 환불건이 반려되었습니다.", this.productName);
            case REFUND_ACCEPT -> String.format("신청하신 <strong>%s</strong> 상품 환불건이 접수되었습니다.", this.productName);
            case REFUND_DONE -> String.format("신청하신 <strong>%s</strong> 상품 환불건이 완료되었습니다.", this.productName);
            case REVIEW_WRITE -> String.format(
                    "%s에 주문하신 <strong>%s</strong> 상품은 어떠셨나요? 만족하셨다면 리뷰 작성하시고 <strong>포인트 받아가세요!</strong>",
                    convertTimestamp2Str(this.orderedAt),
                    this.productName);
            case COUPON_ARRIVED -> String.format(
                    "%s 고객님을 위한 바로피쉬 <strong>%s 쿠폰을 지급</strong>해드렸어요! 지금 바로 마이페이지 쿠폰함에서 확인해보세요:)",
                    this.userName,
                    this.couponName);
            case ADMIN -> this.customContent;
            case INQUIRY_ANSWER -> String.format("문의하신 내용에 답변이 작성되었습니다.");
        };
    }

    public NotificationType getNotificationType(NotificationMessageType type) {
        switch (type) {
            case DELIVER_START:
            case DELIVER_DONE:
            case DELIVER_READY:
                return NotificationType.DELIVERY;
            case ORDER_CANCEL:
            case CANCEL_REJECT:
            case EXCHANGE_REJECT:
            case EXCHANGE_ACCEPT:
            case REFUND_REJECT:
            case REFUND_ACCEPT:
            case REFUND_DONE:
            case PAYMENT_DONE:
                return NotificationType.ORDER;
            case REVIEW_WRITE:
                return NotificationType.REVIEW;
            case COUPON_ARRIVED:
                return NotificationType.COUPON;
            case ADMIN:
            case INQUIRY_ANSWER:
            default:
                return NotificationType.ADMIN;

        }
    }

    public String getNotificationTitle(NotificationMessageType type) {
        switch (type) {
            case PAYMENT_DONE:
                return "결제 완료";
            case DELIVER_READY:
                return "배송 준비";
            case DELIVER_START:
                return "배송 출발";
            case DELIVER_DONE:
                return "배송 완료";
            case ORDER_CANCEL:
                return "주문 취소";
            case CANCEL_REJECT:
                return "주문 취소 반려";
            case EXCHANGE_REJECT:
                return "교환 반려";
            case EXCHANGE_ACCEPT:
                return "교환 접수";
            case REFUND_REJECT:
                return "반품 반려";
            case REFUND_ACCEPT:
                return "반품 접수";
            case REFUND_DONE:
                return "반품 완료";
            case REVIEW_WRITE:
                return "리뷰 작성";
            case COUPON_ARRIVED:
                return "쿠폰 도착";
            case INQUIRY_ANSWER:
                return "문의 답변";
            case ADMIN:
            default:
                return "공지";

        }
    }


}
