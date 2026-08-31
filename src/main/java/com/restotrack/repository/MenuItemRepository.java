package com.restotrack.repository;

import com.restotrack.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Load all menu items with their recipe lines and each line's ingredient
     * eagerly, so the web layer can render them after the session closes
     * (the app runs with open-in-view disabled). {@code distinct} collapses the
     * row multiplication caused by the collection join.
     */
    @Query("select distinct m from MenuItem m left join fetch m.recipe r left join fetch r.ingredient")
    List<MenuItem> findAllWithRecipe();

    @Query("select distinct m from MenuItem m left join fetch m.recipe r left join fetch r.ingredient where m.id = :id")
    Optional<MenuItem> findByIdWithRecipe(@Param("id") Long id);
}
