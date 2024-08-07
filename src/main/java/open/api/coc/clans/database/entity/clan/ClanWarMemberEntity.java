package open.api.coc.clans.database.entity.clan;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.NonNull;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "tb_clan_war_member",
    indexes = {
    @Index(name = "CWM_IDX_01", columnList = "war_id, map_position")
}
)
public class ClanWarMemberEntity implements Persistable<ClanWarMemberPKEntity> {

    @EmbeddedId
    private ClanWarMemberPKEntity id;

    @Column(name = "map_position")
    private Integer mapPosition;

    @Column(name = "name")
    private String name;

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "clanWarMember", cascade = CascadeType.ALL)
    private List<ClanWarMemberAttackEntity> attacks = new ArrayList<>();

    @MapsId(value = "warId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "war_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ClanWarEntity clanWar;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @NonNull
    @Override
    public ClanWarMemberPKEntity getId() {
        return this.id;
    }

    public void changeClanWar(ClanWarEntity clanWarEntity) {
        this.clanWar = clanWarEntity;
    }

    public void addAttacks(ClanWarMemberAttackEntity clanWarMemberAttackEntity) {
        this.attacks.add(clanWarMemberAttackEntity);
        clanWarMemberAttackEntity.changeClanWarMember(this);
    }
}
