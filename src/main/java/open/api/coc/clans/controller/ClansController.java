package open.api.coc.clans.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import open.api.coc.clans.service.ClansService;
import open.api.coc.external.coc.domain.ClanAttackerRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("clans")
public class ClansController {

    private final ClansService clansService;

    @GetMapping("{clanTag}")
    public ResponseEntity<?> findClan(@PathVariable String clanTag) {
        Map<String, Object> clan = clansService.findClanByClanTag(clanTag);
        return ResponseEntity.ok().body(clan);
    }

    @GetMapping("{clanTag}/capitalraidseasons/attack/count")
    public ResponseEntity<?> findClanCapitalRaidSeasons(@PathVariable String clanTag) {
        ClanAttackerRes clanCapitalRaidAttacker = clansService.findClanCapitalRaidSeasonsByClanTagAndLimit(clanTag, 1);
        return ResponseEntity.ok().body(clanCapitalRaidAttacker);
    }

    @GetMapping("capital/attack/count")
    public ResponseEntity<?> getCapitalAttackers() throws ExecutionException, InterruptedException {
        List<ClanAttackerRes> capitalAttackersMap = clansService.getCapitalAttackers();
        return ResponseEntity.ok().body(capitalAttackersMap);
    }
}
