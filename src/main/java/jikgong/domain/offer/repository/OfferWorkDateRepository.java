package jikgong.domain.offer.repository;

import java.util.List;
import java.util.Optional;
import jikgong.domain.offer.entity.OfferWorkDate;
import jikgong.domain.offer.entity.OfferWorkDateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferWorkDateRepository extends JpaRepository<OfferWorkDate, Long> {

    /**
     * 받은 일자리 제안
     */
    @Query(
        "select ow from OfferWorkDate ow join fetch ow.offer.jobPost j join fetch ow.offer o join fetch ow.workDate w "
            +
            "where o.worker.id = :workerId and ow.offerWorkDateStatus = 'OFFER_PENDING' and o.offerStatus = 'OFFER'")
    List<OfferWorkDate> findReceivedPendingOffer(@Param("workerId") Long workerId);

    @Query("select ow from OfferWorkDate ow join fetch ow.offer.jobPost j join fetch "
        + "ow.offer o join fetch ow.workDate w " +
        "where o.worker.id = :workerId and ow.offerWorkDateStatus != 'OFFER_PENDING'")
    List<OfferWorkDate> findReceivedClosedOffer(@Param("workerId") Long workerId);

    @Query("select ow from OfferWorkDate ow join fetch ow.workDate w join fetch w.jobPost j join fetch j.member m " +
        "where ow.id = :offerWorkDateId")
    Optional<OfferWorkDate> findByIdAtProcessOffer(@Param("offerWorkDateId") Long offerWorkDateId);

    @Query("select ow from OfferWorkDate ow join fetch ow.workDate w where ow.id = :offerWorkDateId")
    Optional<OfferWorkDate> findByIdWithWorkDate(@Param("offerWorkDateId") Long offerWorkDateId);


    /**
     * 일자리 제안
     */
    @Modifying
    @Query("update OfferWorkDate ow set ow.offerWorkDateStatus = :status where ow.offer.id = :offerId")
    int cancelOffer(@Param("offerId") Long offerId, @Param("status") OfferWorkDateStatus status);
}
