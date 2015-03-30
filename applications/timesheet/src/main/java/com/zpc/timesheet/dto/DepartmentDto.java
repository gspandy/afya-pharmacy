package com.zpc.timesheet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Administrator on 10/25/2014.
 */
@Getter
@Setter
@NoArgsConstructor
public class DepartmentDto {
    private String departmentId;
    private String departmentName;
    private String status;
}
