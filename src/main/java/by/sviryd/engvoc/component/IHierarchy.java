package by.sviryd.engvoc.component;

import by.sviryd.engvoc.domain.IIdParent;

import java.util.List;
import java.util.Map;

public interface IHierarchy<T extends IIdParent> {
    List<Long> getRootIds();

    List<T> getParents(Long id);

    List<T> getChilds(Long id);

    List<Long> getParentsIds(Long id);

    List<Long> getChildsIds(Long id);

    Map<Long, List<Long>> getChildMapIds();

    Map<Long, List<Long>> getParentMapIds();

    Map<Long, T> getMap();

    List<T> getAll();

    T getById(Long id);
}
