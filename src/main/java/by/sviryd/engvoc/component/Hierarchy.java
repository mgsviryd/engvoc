package by.sviryd.engvoc.component;


import by.sviryd.engvoc.domain.IIdParent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Hierarchy<T extends IIdParent> implements IHierarchy<T> {
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Map<Long, Node> hierarchy;
    private List<T> root;
    private List<T> all;
    private Map<Long, T> map;
    private Map<Long, List<Long>> parentMapIds;
    private Map<Long, List<Long>> childMapIds;
    private List<Long> rootIds;


    public Hierarchy(List<T> categories, Comparator<T> comparator, List<Predicate<T>> filters) {
        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            this.hierarchy = new HashMap<>();
            this.root = new ArrayList<>();
            this.all = categories;
            fillHierarchy(categories, filters);
            adjustHierarchy();
            sortChild(comparator);
            sortRoot(comparator);

            buildRootIds();
            buildChildMapIds();
            buildParentMapIds();
            buildMap();
        } finally {
            lock.unlock();
        }
    }

    private void fillHierarchy(List<T> categories, List<Predicate<T>> filters) {
        for (T c : categories) {
            if (isPassFilters(c, filters)) {
                putAsNode(c);
            }
        }
    }

    private boolean isPassFilters(T c, List<Predicate<T>> filters) {
        if (filters == null) {
            return true;
        }
        for (Predicate<T> p : filters) {
            if (!p.test(c)) {
                return false;
            }
        }
        return true;
    }

    private void putAsNode(T c) {
        hierarchy.put(c.getId(), new Node(c, null, new ArrayList<>()));
    }

    private void adjustHierarchy() {
        hierarchy.values().forEach(node -> {
            Long parentId = node.getCategory().getParent();
            if (new Long(0).equals(parentId)) {
                root.add((T) node.getCategory());
                return;
            }
            Node parentNode = hierarchy.get(parentId);
            parentNode.addChild(node.getCategory());
            node.setParent(parentNode.getCategory());
        });
    }

    private void sortChild(Comparator<T> comparator) {
        hierarchy.values().forEach(node -> node.sortChild(comparator));
    }

    private void sortRoot(Comparator<T> comparator) {
        root.sort(comparator);
    }

    private void buildRootIds() {
        this.rootIds = root.stream().map(IIdParent::getId).collect(Collectors.toList());
    }

    private void buildChildMapIds() {
        Map<Long, List<Long>> map = new HashMap<>();
        Map<Long, List<T>> childMap = getChildMap();
        for (Map.Entry<Long, List<T>> entrySet : childMap.entrySet()) {
            List<Long> ids = entrySet.getValue().stream().map(IIdParent::getId).collect(Collectors.toList());
            map.put(entrySet.getKey(), ids);
        }
        this.childMapIds = map;
    }


    private void buildParentMapIds() {
        Map<Long, List<Long>> map = new HashMap<>();
        for (Long id : hierarchy.keySet()) {
            map.put(id, getParentsIds(id));
        }
        this.parentMapIds = map;
    }

    private void buildMap() {
        this.map = all.stream().collect(Collectors.toMap(IIdParent::getId, y -> y));
    }

    @Override
    public List<T> getChilds(Long id) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            Node node = hierarchy.get(id);
            return node.getChild();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public List<Long> getChildsIds(Long id) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            Node node = hierarchy.get(id);
            List<T> child = node.getChild();
            return child.stream().map(IIdParent::getId).collect(Collectors.toList());
        } finally {
            lock.unlock();
        }

    }

    @Override
    public List<T> getParents(Long id) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            Deque<T> queue = new LinkedList<>();
            T category = (T) hierarchy.get(id).getCategory();
            queue.addFirst(category);
            Long currId = category.getParent();
            Long root = 0L;
            while (!root.equals(currId)) {
                category = (T) hierarchy.get(currId).getCategory();
                queue.addFirst(category);
                currId = category.getParent();
            }
            return new ArrayList<>(queue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Long> getParentsIds(Long id) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            Deque<Long> queue = new LinkedList<>();
            T category = (T) hierarchy.get(id).getCategory();
            queue.addFirst(category.getId());
            Long currId = category.getParent();
            Long root = 0L;
            while (!root.equals(currId)) {
                category = (T) hierarchy.get(currId).getCategory();
                queue.addFirst(category.getId());
                currId = category.getParent();
            }
            return new ArrayList<>(queue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Long> getRootIds() {
        return this.rootIds;
    }

    @Override
    public Map<Long, List<Long>> getChildMapIds() {
        return this.childMapIds;
    }

    private Map<Long, List<T>> getChildMap() {
        return hierarchy.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getChild()));
    }

    @Override
    public Map<Long, List<Long>> getParentMapIds() {
        return this.parentMapIds;
    }

    @Override
    public Map<Long, T> getMap() {
        return this.map;
    }

    @Override
    public List<T> getAll() {
        return all;
    }

    @Override
    public T getById(Long id){
        return map.get(id);
    }


    @Getter
    @Setter
    @AllArgsConstructor
    private static class Node<T extends IIdParent> {
        private T category;
        private T parent;
        private List<T> child;

        private void addChild(T c) {
            child.add(c);
        }

        private void sortChild(Comparator<T> comparator) {
            child.sort(comparator);
        }
    }
}
