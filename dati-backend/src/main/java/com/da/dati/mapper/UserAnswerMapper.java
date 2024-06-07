package com.da.dati.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.da.dati.model.dto.statistic.AppAnswerCountDTO;
import com.da.dati.model.dto.statistic.AppAnswerResultCountDTO;
import com.da.dati.model.entity.UserAnswer;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 13491
* @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
* @createDate 2024-05-26 02:13:53
* @Entity com.da.dati.model.entity.UserAnswer
*/
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    @Select("select appId, count(user_answer.userId) as answerCount from user_answer  group by appId order by answerCount desc limit 10;")
    List<AppAnswerCountDTO> doAppAnswerCount();

    @Select("select resultName, count(resultName) as resultCount from user_answer where appId = #{appId} group by resultName order by resultCount desc;")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);
}




