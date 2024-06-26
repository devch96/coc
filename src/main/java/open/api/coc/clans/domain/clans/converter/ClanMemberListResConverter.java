package open.api.coc.clans.domain.clans.converter;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import open.api.coc.clans.domain.clans.ClanMemberListRes;
import open.api.coc.external.coc.clan.domain.clan.ClanMemberList;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class ClanMemberListResConverter implements Converter<ClanMemberList, ClanMemberListRes> {

    private final ClanMemberResponseConverter clanMemberResponseConverter;

    @Override
    public @NonNull ClanMemberListRes convert(ClanMemberList source) {
        if (CollectionUtils.isEmpty(source.getItems())) {
            return ClanMemberListRes.empty();
        }

        return ClanMemberListRes.create(source.getItems()
                                              .stream()
                                              .map(clanMemberResponseConverter::convert)
                                              .collect(Collectors.toList()));

    }
}