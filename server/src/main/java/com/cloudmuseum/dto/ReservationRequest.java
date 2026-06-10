package com.cloudmuseum.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequest {
    @NotNull(message = "参观日期不能为空")
    @FutureOrPresent(message = "参观日期必须为今天或将来")
    private LocalDate visitDate;

    @NotBlank(message = "参观时段不能为空")
    private String visitTimeSlot;

    @NotBlank(message = "参观人姓名不能为空")
    @Size(max = 50, message = "姓名最长50字")
    private String visitorName;

    @NotBlank(message = "参观人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String visitorPhone;

    @Min(value = 1, message = "参观人数至少1人")
    @Max(value = 20, message = "单次预约最多20人")
    private Integer visitorCount;

    private String idCard;

    @Size(max = 500, message = "备注最长500字")
    private String notes;
}
