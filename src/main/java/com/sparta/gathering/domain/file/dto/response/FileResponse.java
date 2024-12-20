package com.sparta.gathering.domain.file.dto.response;

import com.sparta.gathering.domain.file.entity.File;
import lombok.Getter;

@Getter
public class FileResponse {
    private final Long id;
    private final String originName;
    private final String name;
    private final String uri;

    public FileResponse(File file) {
        this.id = file.getId();
        this.originName = file.getOriginName();
        this.name = file.getName();
        this.uri = file.getUri();
    }
}
