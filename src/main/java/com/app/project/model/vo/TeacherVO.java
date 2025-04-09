package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Teacher;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 教师信息视图
 *
 * @author
 * @from
 */
@Data
public class TeacherVO extends UserVO {
    private String teacherNumber;  // 编号
    private String job;            // 职务
    private String qualification;  // 学历

}
