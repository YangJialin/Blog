package ink.helloworld.halo.service.impl;

import ink.helloworld.halo.model.domain.Menu;
import ink.helloworld.halo.repository.MenuRepository;
import ink.helloworld.halo.service.MenuService;
import ink.helloworld.halo.service.base.AbstractCrudService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 *     菜单业务逻辑实现类
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2018/1/24
 */
@Service
public class MenuServiceImpl extends AbstractCrudService<Menu, Long> implements MenuService {

    private static final String MENUS_CACHE_KEY = "'menu'";

    private static final String MENUS_CACHE_NAME = "menus";

    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        super(menuRepository);
        this.menuRepository = menuRepository;
    }

    /**
     * 查询所有菜单
     *
     * @return List
     */
    @Override
    @Cacheable(value = MENUS_CACHE_NAME, key = MENUS_CACHE_KEY)
    public List<Menu> listAll() {
        return super.listAll();
    }

    /**
     * 新增/修改菜单
     *
     * @param menu menu
     * @return Menu
     */
    @Override
    @CacheEvict(value = MENUS_CACHE_NAME, allEntries = true, beforeInvocation = true)
    public Menu create(Menu menu) {
        return super.create(menu);
    }

    /**
     * 删除菜单
     *
     * @param menuId menuId
     * @return Menu
     */
    @Override
    @CacheEvict(value = MENUS_CACHE_NAME, allEntries = true, beforeInvocation = true)
    public Menu removeById(Long menuId) {
        return super.removeById(menuId);
    }

}
