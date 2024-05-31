package ru.kirill.portalService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.model.Car;
import ru.kirill.portalService.model.User;
import ru.kirill.portalService.repositories.CarRepository;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;
    public void addCar(Car car, User user){
        if(!user.getClientRoles().containsKey(car.getCompany()))
            return;

        if(!user.getClientRoles().get(car.getCompany()).equals("ADMIM") &&
                !user.getClientRoles().get(car.getCompany()).equals("LOGIST"))
            return;

        try {
            if(carRepository.findByVin(car.getVin()).getCompany() != null)
                return;
        } catch (NullPointerException e){
        }

        carRepository.save(car);
    }
}
