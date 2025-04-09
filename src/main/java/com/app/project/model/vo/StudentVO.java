package com.app.project.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 学生信息视图
 *
 * @author
 * @from
 */
@Data
public class StudentVO extends UserVO {


    /**
     * 学号
     */
    private String studentNumber;


}
