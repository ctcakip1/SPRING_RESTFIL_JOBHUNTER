package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void hanleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserById(long id) {
        return this.userRepository.findById(id);
    }

    public List<User> handleGetAllUser() {
        return this.userRepository.findAll();
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.userRepository.findById(user.getId());
        if (currentUser != null) {
            currentUser.setEmail(user.getEmail());
            currentUser.setName(user.getName());
            currentUser.setPassword(user.getPassword());
            this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username){
        return this.userRepository.findByEmail(username);
    }
}
