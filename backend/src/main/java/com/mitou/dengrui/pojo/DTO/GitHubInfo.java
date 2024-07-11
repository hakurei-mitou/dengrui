package com.mitou.dengrui.pojo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GitHubInfo {
    String username;

    @JsonProperty("repo_name")
    String repoName;

    @JsonProperty("access_token")
    String accessToken;
}
