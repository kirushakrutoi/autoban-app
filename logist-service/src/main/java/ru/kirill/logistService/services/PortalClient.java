package ru.kirill.logistService.services;


import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.kirill.logistService.models.DTOs.CarDTO;
import ru.kirill.logistService.models.DTOs.CompanyDTO;
import ru.kirill.logistService.models.DTOs.DriverDTO;
import ru.kirill.logistService.models.resources.CarResource;
import ru.kirill.logistService.models.resources.DriverResource;

import java.util.Map;

public interface PortalClient {
    @RequestLine("POST /user/get/driver/{driver_id}")
    DriverDTO findDriverById(@Param("driver_id") String driverId,
                             @HeaderMap Map<String, String> headers,
                             CompanyDTO companyDTO);

    @RequestLine("POST /car/{car_id}")
    CarDTO findCarById(@Param("car_id") long carId,
                       @HeaderMap Map<String, String> headers,
                       CompanyDTO companyDTO);
}
