package by.temniakov.english.tracker.store.repositories;

import by.temniakov.english.tracker.store.entities.PhrasalVerbEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhrasalVerbRepository extends JpaRepository<PhrasalVerbEntity,Long> {

    @Query("select pv.verbText " +
            "from PhrasalVerbEntity pv order by random()")
     List<String> verbTextOrderByRandom(Pageable pageable);

    @Query("select pv.verbText " +
            "from PhrasalVerbEntity pv " +
            "where lower(pv.verbText) like  concat(lower(:prefixText),'%')" +
            "order by pv.verbText asc ")
    List<String> verbTextStartsWithIgnoreCaseOrderByVerbText(
            @Param("prefixText") String prefixText, Pageable pageable);
}
