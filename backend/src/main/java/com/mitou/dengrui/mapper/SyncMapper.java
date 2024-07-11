package com.mitou.dengrui.mapper;

import com.mitou.dengrui.pojo.DTO.GitHubInfo;
import com.mitou.dengrui.pojo.entity.Card;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SyncMapper {
    @Select("select username, repo_name, access_token from github_info where id = 1")
    GitHubInfo getInfo();

    @Update("update github_info set username = #{username}, repo_name = #{repoName}, access_token = #{accessToken} where id = 1")
    void setInfo(GitHubInfo gitHubInfo);
}
