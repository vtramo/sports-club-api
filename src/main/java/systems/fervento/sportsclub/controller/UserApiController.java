package systems.fervento.sportsclub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import systems.fervento.sportsclub.mapper.NotificationApiMapper;
import systems.fervento.sportsclub.mapper.UserApiMapper;
import systems.fervento.sportsclub.openapi.api.UsersApi;
import systems.fervento.sportsclub.openapi.model.NotificationPage;
import systems.fervento.sportsclub.openapi.model.User;
import systems.fervento.sportsclub.service.UserService;

import java.util.Optional;

@RestController
public class UserApiController implements UsersApi {
    private final UserService userService;
    private final UserApiMapper userApiMapper = UserApiMapper.INSTANCE;
    private final NotificationApiMapper notificationApiMapper = NotificationApiMapper.INSTANCE;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<User> getUserById(Long userId) {
        return UsersApi.super.getUserById(userId);
    }

    @Override
    public ResponseEntity<User> registerUser(User user) {
        var userData = userApiMapper.mapToUserData(user);
        var createdUserData = userService.registerUser(userData);
        var createdUser = userApiMapper.mapToUserApi(createdUserData);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<NotificationPage> getUserNotifications(
        Long userId,
        Integer pageNo,
        Integer pageSize,
        Boolean hasBeenRead
    ) {
        final Optional<Boolean> hasBeenReadQueryParam = Optional.ofNullable(hasBeenRead);
        var userNotificationsDataPage =
                userService.getAllUserNotificationsByUserId(pageNo, pageSize, userId, hasBeenReadQueryParam);
        return ResponseEntity.ok(
            notificationApiMapper
                .mapToNotificationPage(userNotificationsDataPage)
        );
    }
}
