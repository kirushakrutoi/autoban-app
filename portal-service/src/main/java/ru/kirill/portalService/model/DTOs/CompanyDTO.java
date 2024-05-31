package ru.kirill.portalService.model.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class CompanyDTO {
    private String name;
    private String inn;
    private String address;
    private String kpp;
    private String ogrn;

    @SuppressWarnings("unchecked")
    @JsonProperty("suggestions")
    private void unpackNested(List<Object> suggestions) {
        try {
            Map<String, Object> suggestion = (Map<String, Object>) suggestions.get(0);
            this.name = (String) suggestion.get("value");
            Map<String, Object> data = (Map<String, Object>) suggestion.get("data");
            this.inn = (String) data.get("inn");
            this.ogrn = (String) data.get("ogrn");
            this.kpp = (String) data.get("kpp");
            Map<String, Object> address = (Map<String, Object>) data.get("address");
            this.address = (String) address.get("value");
        } catch (Exception ignored){
        }
    }
}
