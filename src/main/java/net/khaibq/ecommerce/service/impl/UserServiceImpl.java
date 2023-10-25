package net.khaibq.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.khaibq.ecommerce.config.properties.LocalCacheConfigurationProperties;
import net.khaibq.ecommerce.dtos.EmailDto;
import net.khaibq.ecommerce.dtos.auth.ChangePwDto;
import net.khaibq.ecommerce.dtos.auth.ForgotPwDto;
import net.khaibq.ecommerce.dtos.auth.RegisterUserDto;
import net.khaibq.ecommerce.dtos.user.SearchUserDto;
import net.khaibq.ecommerce.dtos.user.UserDto;
import net.khaibq.ecommerce.entity.User;
import net.khaibq.ecommerce.entity.User_;
import net.khaibq.ecommerce.exception.BaseException;
import net.khaibq.ecommerce.mapping.UserMapper;
import net.khaibq.ecommerce.repository.UserRepository;
import net.khaibq.ecommerce.service.UserService;
import net.khaibq.ecommerce.utils.EmailUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final LocalCacheConfigurationProperties properties;
    private final CacheManager cacheManager;
    private final EmailUtils emailUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(RegisterUserDto dto) {
        if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new BaseException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new BaseException("Email đã tồn tại");
        }
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .status(0)
                .isDeleted(0)
                .build();
        userRepository.save(user);

        String key = RandomStringUtils.randomAlphabetic(40);
        Objects.requireNonNull(cacheManager.getCache(properties.getCacheNames())).put(key, dto.getEmail());

        EmailDto emailDto = EmailDto.builder()
                .emailTo(new String[]{dto.getEmail()})
                .subject("Chào mừng bạn đến với khaibq.net")
                .template("email/email-active-account.html")
                .params(Map.of("email", dto.getEmail(), "link", "http://localhost:8080/auth/register/active/" + key))
                .build();
        CompletableFuture<Boolean> emailStatus = emailUtils.sendEmail(emailDto);
        emailStatus.thenAccept(status -> {
            if (Boolean.TRUE.equals(status)) {
                log.info("Email sent successfully.");
            } else {
                log.info("Failed to send email.");
            }
        });
    }

    @Override
    public void activeUser(String key) {
        var cacheValue = getCacheValue(key);
        String email = (String) cacheValue.get();
        User user = Optional.ofNullable(userRepository.findByEmailIgnoreCase(email))
                .orElseThrow(() -> new BaseException("Không tìm thấy thông tin tài khoản. Vui lòng thử lại sau"));
        user.setStatus(1);
        userRepository.save(user);
    }

    @Override
    public void changePw(String key, ChangePwDto dto) {
        var cacheValue = getCacheValue(key);
        if (!Objects.equals(dto.getPassword(), dto.getConfirmPassword())){
            throw new BaseException("Mật khẩu xác nhận không trùng khớp");
        }
        String email = (String) cacheValue.get();
        User user = Optional.ofNullable(userRepository.findByEmailIgnoreCase(email))
                .orElseThrow(() -> new BaseException("Không tìm thấy thông tin tài khoản. Vui lòng thử lại sau"));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void forgotPw(ForgotPwDto dto) {
        String email = dto.getEmail();
        Optional.ofNullable(userRepository.findByEmailIgnoreCase(email))
                .orElseThrow(() -> new BaseException("Email không tồn tại. Vui lòng kiểm tra và thực hiện lại"));

        String key = RandomStringUtils.randomAlphabetic(40);
        Objects.requireNonNull(cacheManager.getCache(properties.getCacheNames())).put(key, dto.getEmail());

        EmailDto emailDto = EmailDto.builder()
                .emailTo(new String[]{dto.getEmail()})
                .subject("Đặt lại mật khẩu tại khaibq.net")
                .template("email/email-forgot-pw.html")
                .params(Map.of("email", dto.getEmail(), "link", "http://localhost:8080/auth/recoverpw/" + key))
                .build();
        CompletableFuture<Boolean> emailStatus = emailUtils.sendEmail(emailDto);
        emailStatus.thenAccept(status -> {
            if (Boolean.TRUE.equals(status)) {
                log.info("Email sent successfully.");
            } else {
                log.info("Failed to send email.");
            }
        });

    }


    @Override
    public Page<UserDto> getByPage(SearchUserDto searchUserDto, Pageable pageable) {
        Specification<User> spec = Specification.where(null);
        if (StringUtils.isNotEmpty(searchUserDto.getSearchText())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.upper(root.get(User_.username)), "%" + searchUserDto.getSearchText().toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get(User_.fullName)), "%" + searchUserDto.getSearchText().toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get(User_.email)), "%" + searchUserDto.getSearchText().toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get(User_.phoneNumber)), "%" + searchUserDto.getSearchText().toLowerCase() + "%")
                    ));
        }
        if (searchUserDto.getStatus() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(User_.IS_DELETED), searchUserDto.getStatus()));
        }
        return userRepository.findAll(spec, pageable).map(mapper::toDto);
    }

    private Cache.ValueWrapper getCacheValue(String key) {
        Cache.ValueWrapper cacheValue = Objects.requireNonNull(cacheManager.getCache(properties.getCacheNames())).get(key);
        if (cacheValue == null) {
            throw new BaseException("Đường dẫn không đúng hoặc đã hết hạn");
        }
        return cacheValue;
    }
}
