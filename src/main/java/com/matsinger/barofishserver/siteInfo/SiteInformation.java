package com.matsinger.barofishserver.siteInfo;

import jakarta.persistence.*;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "site_information", schema = "barofish_dev", catalog = "")
public class SiteInformation {
    @Id
    @Column(name = "id", nullable = false, length = 30)
    private String id;

    @Basic
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;

    public SiteInfoDto convert2Dto() {
        SiteInfoDto info = SiteInfoDto.builder().id(this.id).description(this.description)
                .content(this.id.startsWith("TC") ? null : this.content).build();
        if (this.id.startsWith("TC")) {
            List<SiteInfoController.TitleContentReq> tcs = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(this.content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                SiteInfoController.TitleContentReq tc = SiteInfoController.TitleContentReq.builder()
                        .title(obj.getString("title")).content(obj.getString(
                                "content"))
                        .build();
                tcs.add(tc);
            }
            info.setTcContent(tcs);
        }
        return info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SiteInformation that = (SiteInformation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, content);
    }
}
