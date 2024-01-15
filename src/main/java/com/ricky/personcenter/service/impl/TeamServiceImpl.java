package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.personcenter.model.entity.Team;
import generator.service.TeamService;
import com.ricky.personcenter.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Ricky
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-01-15 11:19:47
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




