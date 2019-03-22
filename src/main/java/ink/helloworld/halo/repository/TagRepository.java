package ink.helloworld.halo.repository;

import ink.helloworld.halo.model.domain.Tag;
import ink.helloworld.halo.repository.base.BaseRepository;

/**
 * <pre>
 *     标签持久层
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2018/1/12
 */
public interface TagRepository extends BaseRepository<Tag, Long> {

    /**
     * 根据标签路径查询，用于验证是否已经存在该路径
     *
     * @param tagUrl tagUrl
     * @return Tag
     */
    Tag findTagByTagUrl(String tagUrl);

    /**
     * 根据标签名称查询
     *
     * @param tagName 标签名
     * @return Tag
     */
    Tag findTagByTagName(String tagName);
}
