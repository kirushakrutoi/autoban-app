package ru.kirill.portalService.model.DTOs;

import lombok.Data;

@Data
public class GetPagingDTO {
    private int page;
    private int pageSize;
}
