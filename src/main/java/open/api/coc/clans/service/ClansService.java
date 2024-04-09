package open.api.coc.clans.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import open.api.coc.clans.common.ExceptionCode;
import open.api.coc.clans.common.exception.CustomRuntimeException;
import open.api.coc.clans.domain.ClanCapitalAttackerRes;
import open.api.coc.clans.domain.ClanCapitalUnderAttackerRes;
import open.api.coc.external.coc.clan.ClanApiService;
import open.api.coc.external.coc.clan.domain.ClanCapitalRaidSeason;
import open.api.coc.external.coc.clan.domain.ClanCapitalRaidSeasonMember;
import open.api.coc.external.coc.clan.domain.ClanCapitalRaidSeasons;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClansService {

    private final ClanApiService clanApiService;


    public Map<String, Object> findClanByClanTag(String clanTag) {
        return clanApiService.findClanByClanTag(clanTag);
    }

    private List<ClanCapitalRaidSeasonMember> getClanCapitalRaidSeasonsMembers(String clanTag, int limit) {
        ClanCapitalRaidSeasons clanCapitalRaidSeasons = clanApiService.findClanCapitalRaidSeasonsByClanTagAndLimit(clanTag, limit)
                                                                      .orElseThrow(() -> CustomRuntimeException.create(ExceptionCode.EXTERNAL_ERROR, "클랜캐피탈 조회 실패"));

        if (clanCapitalRaidSeasons.isEmpty()) {
            return Collections.emptyList();
        }

        ClanCapitalRaidSeason clanCapitalRaidSeason = clanCapitalRaidSeasons.getItemWithMembers();

        return clanCapitalRaidSeason.getMembers();
    }

    public ClanCapitalAttackerRes findClanCapitalRaidSeasons(String clanTag) {
        final int SEARCH_LIMIT = 1;
        List<ClanCapitalRaidSeasonMember> members = getClanCapitalRaidSeasonsMembers(clanTag, SEARCH_LIMIT);
        return ClanCapitalAttackerRes.create(clanTag, members.size());
    }

    public ClanCapitalUnderAttackerRes getClanCapitalUnderAttackers(String clanTag, int limit) {
        List<ClanCapitalRaidSeasonMember> members = getClanCapitalRaidSeasonsMembers(clanTag, limit);
        List<ClanCapitalRaidSeasonMember> underAttackers = findUnderAttackers(members);
        return ClanCapitalUnderAttackerRes.create(clanTag, underAttackers);
    }

    private List<ClanCapitalRaidSeasonMember> findUnderAttackers(List<ClanCapitalRaidSeasonMember> members) {
        if (ObjectUtils.isEmpty(members)) return Collections.emptyList();

        return members.stream()
                      .filter(ClanCapitalRaidSeasonMember::isUnderAttacks)
                      .collect(Collectors.toList());
    }

    public List<ClanCapitalAttackerRes> getCapitalAttackers() throws ExecutionException, InterruptedException {
        List<String> CLAN_TAGS = ClanCapitalAttackerRes.CLAN_TAGS.keySet().stream().toList();

        ForkJoinPool forkJoinPool = new ForkJoinPool(CLAN_TAGS.size());
        List<ClanCapitalAttackerRes> clanAttackers = forkJoinPool.submit(() -> CLAN_TAGS.stream()
                                                                                        .parallel()
                                                                                        .map(this::findClanCapitalRaidSeasons)
                                                                                        .collect(Collectors.toList()))
                                                                 .get();

        return clanAttackers.stream()
                            .sorted(Comparator.comparing(ClanCapitalAttackerRes::getName))
                            .collect(Collectors.toList());
    }

    public List<ClanCapitalUnderAttackerRes> getCapitalMissingAttackers() throws ExecutionException, InterruptedException {
        List<String> CLAN_TAGS = ClanCapitalAttackerRes.CLAN_TAGS.keySet().stream().toList();

        ForkJoinPool forkJoinPool = new ForkJoinPool(CLAN_TAGS.size());

        return forkJoinPool.submit(() -> CLAN_TAGS.stream()
                                                  .parallel()
                                                  .map(clanTag -> getClanCapitalUnderAttackers(clanTag, 1))
                                                  .collect(Collectors.toList()))
                           .get();
    }
}
