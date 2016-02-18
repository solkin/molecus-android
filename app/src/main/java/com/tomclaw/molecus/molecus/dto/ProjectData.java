package com.tomclaw.molecus.molecus.dto;

import com.tomclaw.molecus.util.Unobfuscatable;

import java.util.List;

/**
 * Created by Solkin on 04.11.2015.
 */
public class ProjectData implements Unobfuscatable {

    private List<String> url_list;
    private String html;
    private String plain;

    public ProjectData() {
    }

    public List<String> getUrlList() {
        return url_list;
    }

    public String getHtml() {
        return html;
    }

    public String getPlain() {
        return plain;
    }
}
