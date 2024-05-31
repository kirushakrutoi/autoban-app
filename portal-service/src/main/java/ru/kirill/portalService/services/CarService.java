package ru.kirill.portalService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.exceptions.CarAlreadyExistException;
import ru.kirill.portalService.exceptions.userexception.ForbiddenException;
import ru.kirill.portalService.model.Car;
import ru.kirill.portalService.model.User;
import ru.kirill.portalService.repositories.CarRepository;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;
    public void addCar(Car car, User user) throws ForbiddenException, CarAlreadyExistException {
        if(!user.getClientRoles().containsKey(car.getCompany()))
            throw new ForbiddenException("Forbidden");

        if(!user.getClientRoles().get(car.getCompany()).equals("ADMIM") &&
                !user.getClientRoles().get(car.getCompany()).equals("LOGIST"))
            throw new ForbiddenException("Forbidden");

        try {
            if(carRepository.findByVin(car.getVin()).getCompany() != null)
                throw new CarAlreadyExistException("Car already exist");
        } catch (NullPointerException e){
        }

        carRepository.save(car);
    }
}
