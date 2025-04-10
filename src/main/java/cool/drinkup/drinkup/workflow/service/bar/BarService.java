package cool.drinkup.drinkup.workflow.service.bar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import cool.drinkup.drinkup.user.service.UserService;
import cool.drinkup.drinkup.workflow.controller.req.BarCreateReq;
import cool.drinkup.drinkup.workflow.mapper.BarMapper;
import cool.drinkup.drinkup.workflow.model.Bar;
import cool.drinkup.drinkup.workflow.repository.BarRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BarService {

    private final BarMapper barMapper;
    private final BarRepository barRepository;
    private final UserService userService;

    public Bar createBar(BarCreateReq barCreateReq) {
        Bar bar = barMapper.toBar(barCreateReq);
        return barRepository.save(bar);
    }

    @Transactional(readOnly = true)
    public List<Bar> getUserBar(String username) {
        return userService.findByUsername(username)
                .map(user -> {
                    List<Bar> bars = barRepository.findByUserId(user.getId());
                    return bars;
                })
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public List<Bar> getUserBarByBarIds(List<Long> barIds) {
        if (CollectionUtils.isEmpty(barIds)) {
            return new ArrayList<>();
        }
        return barRepository.findAllById(barIds);
    }
}
