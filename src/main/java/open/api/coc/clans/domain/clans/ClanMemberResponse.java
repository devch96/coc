package open.api.coc.clans.domain.clans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ClanMemberResponse {

    private String name;
    private String tag;
    private String role;
    private Integer townHallLevel;
    private Integer expLevel;

    private LabelResponse league;
    private Integer trophies;
    private Integer clanRank;
    private Integer previousClanRank;
    private Integer donations;
    private Integer donationsReceived;

}
