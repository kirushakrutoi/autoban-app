package ru.kirill.portalService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.exceptions.carexceptions.CarAlreadyExistException;
import ru.kirill.portalService.exceptions.carexceptions.CarNotFoundExceptions;
import ru.kirill.portalService.exceptions.userexception.ForbiddenException;
import ru.kirill.portalService.model.Car;
import ru.kirill.portalService.model.User;
import ru.kirill.portalService.repositories.CarRepository;

import java.util.Optional;

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

    public Car getCar(long id, User user, String companyName) throws CarNotFoundExceptions, ForbiddenException {
        Optional<Car> OCar = carRepository.findById(id);

        if(!OCar.isPresent())
            throw new CarNotFoundExceptions("Car with this id not found");

        Car car = OCar.get();

        if(!user.getClientRoles().get(car.getCompany()).equals("ADMIM") &&
                !user.getClientRoles().get(car.getCompany()).equals("LOGIST") ||
                !car.getCompany().equals(companyName))
            throw new ForbiddenException("Forbidden");

        return car;
    }
}
