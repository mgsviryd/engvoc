package by.sviryd.engvoc.interceptor;

import by.sviryd.engvoc.domain.Category;
import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;

public class BlankIndexingInterceptor implements
        EntityIndexingInterceptor<Category> {
    @Override
    public IndexingOverride onAdd(Category entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }

    @Override
    public IndexingOverride onUpdate(Category entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }

    @Override
    public IndexingOverride onDelete(Category entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }

    @Override
    public IndexingOverride onCollectionUpdate(Category entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }
}
