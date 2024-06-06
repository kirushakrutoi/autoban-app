package ru.kirill.logistService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.logistService.exceptions.ForbiddenException;
import ru.kirill.logistService.exceptions.IncorrectDataException;
import ru.kirill.logistService.exceptions.TaskNotFoundException;
import ru.kirill.logistService.mappers.Mapper;
import ru.kirill.logistService.models.DTOs.CreateRouteDTO;
import ru.kirill.logistService.models.DTOs.TaskDTO;
import ru.kirill.logistService.models.Task;
import ru.kirill.logistService.models.User;
import ru.kirill.logistService.repositories.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public void create(TaskDTO taskDTO, User user) throws ForbiddenException {
        String companyName = taskDTO.getCompanyName();

        if(!checkAuthority(user, companyName, "LOGIST"))
            throw new ForbiddenException("You are not a LOGIST of this company");

        Task task = Mapper.convertToTask(taskDTO);
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task get(long id, User user) throws TaskNotFoundException, ForbiddenException {
        Optional<Task> OTask = taskRepository.findById(id);

        if(!OTask.isPresent())
            throw new TaskNotFoundException("The task with this id was not found");

        Task task = OTask.get();

        if(!checkAuthority(user, task.getCompanyName(), "LOGIST"))
            throw new ForbiddenException("You are not a LOGIST of this company");

        return task;
    }

    @Transactional(readOnly = true)
    public List<Task> getAll(Integer page, Integer pageSize, String companyName, User user) throws IncorrectDataException, ForbiddenException {
        if(!checkAuthority(user, companyName, "LOGIST"))
            throw new ForbiddenException("You are not a LOGIST of this company");

        if(page != null && pageSize != null)
            return taskRepository.findAll(PageRequest.of(page, pageSize)).getContent();

        if(page != null || pageSize != null)
            throw new IncorrectDataException("Page or page size can't be empty");

        return taskRepository.findAllByCompanyName(companyName);
    }

    private boolean checkAuthority(User user, String companyName, String role){
        if(!user.getClientRoles().containsKey(companyName))
            return false;

        if(!user.getClientRoles().get(companyName).equals(role))
            return false;

        return true;
    }
}
