package systems.fervento.sportsclub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import systems.fervento.sportsclub.data.NotificationData;
import systems.fervento.sportsclub.data.UserData;
import systems.fervento.sportsclub.entity.NotificationEntity;
import systems.fervento.sportsclub.exception.ResourceNotFoundException;
import systems.fervento.sportsclub.mapper.NotificationDataMapper;
import systems.fervento.sportsclub.mapper.UserDataMapper;
import systems.fervento.sportsclub.mapper.UserEntityMapper;
import systems.fervento.sportsclub.repository.NotificationRepository;
import systems.fervento.sportsclub.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDataMapper notificationDataMapper;
    private final UserDataMapper userDataMapper;
    private final UserEntityMapper userEntityMapper;

    public UserService(
        UserRepository userRepository,
        NotificationRepository notificationRepository,
        NotificationDataMapper notificationDataMapper,
        UserDataMapper userDataMapper,
        UserEntityMapper userEntityMapper
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.notificationDataMapper = notificationDataMapper;
        this.userDataMapper = userDataMapper;
        this.userEntityMapper = userEntityMapper;
    }

    public UserData registerUser(UserData user) {
        Objects.requireNonNull(user);
        var userEntity = userEntityMapper.mapToUserEntity(user);
        userRepository.save(userEntity);
        return userDataMapper.map(userEntity);
    }

    public Page<NotificationData> getAllUserNotificationsByUserId(
        Integer pageNo,
        Integer pageSize,
        final long ownerId,
        final Optional<Boolean> filterByHasBeenRead
    ) {
        if (!userRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("The provided id doesn't identify any user!");
        }

        final Pageable pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        final Supplier<Page<NotificationEntity>> getUserNotificationsPage = (filterByHasBeenRead.isEmpty())
            ? () -> notificationRepository.findAllByOwnerId(pageRequest, ownerId)
            : () -> notificationRepository.findAllByOwnerIdAndHasBeenRead(pageRequest, ownerId, filterByHasBeenRead.get());

        return getUserNotificationsPage.get()
            .map(notificationDataMapper::mapToNotificationData);
    }

    public UserData getUserById(long userId) {
        return userRepository
            .findById(userId)
            .map(userDataMapper::map)
            .orElseThrow(() -> new ResourceNotFoundException("This user id doesn't identify any user!"));
    }
}
