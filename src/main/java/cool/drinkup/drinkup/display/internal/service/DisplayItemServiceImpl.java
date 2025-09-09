package cool.drinkup.drinkup.display.internal.service;

import cool.drinkup.drinkup.display.api.DisplayItemService;
import cool.drinkup.drinkup.display.internal.controller.resp.DisplayItemVo;
import cool.drinkup.drinkup.display.internal.enums.DisplayType;
import cool.drinkup.drinkup.display.internal.mapper.DisplayItemRepository;
import cool.drinkup.drinkup.display.internal.model.DisplayItem;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DisplayItemServiceImpl implements DisplayItemService {

    private final DisplayItemRepository displayItemRepository;

    @Override
    public List<DisplayItemVo> getActiveDisplayItems() {
        List<DisplayItem> items = displayItemRepository.findActiveItems(ZonedDateTime.now());
        return convertToVoList(items);
    }

    @Override
    public List<DisplayItemVo> getActiveDisplayItemsByType(DisplayType type) {
        List<DisplayItem> items = displayItemRepository.findActiveItemsByType(type, ZonedDateTime.now());
        return convertToVoList(items);
    }

    private List<DisplayItemVo> convertToVoList(List<DisplayItem> items) {
        return items.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    private DisplayItemVo convertToVo(DisplayItem item) {
        DisplayItemVo vo = new DisplayItemVo();
        vo.setType(item.getType());
        vo.setImageUrl(item.getImageUrl());
        vo.setLinkUrl(item.getLinkUrl());
        vo.setContent(item.getContent());
        return vo;
    }
}
