package cool.drinkup.drinkup.common.log;

import org.springframework.stereotype.Service;
import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;

import java.util.Optional;

import cool.drinkup.drinkup.user.spi.AuthenticatedUserDTO;
import cool.drinkup.drinkup.user.spi.AuthenticationServiceFacade;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultOperatorGetServiceImpl  implements IOperatorGetService{

    private final AuthenticationServiceFacade authenticationServiceFacade;

    @Override
    public Operator getUser() {
        Optional<AuthenticatedUserDTO> user = authenticationServiceFacade.getCurrentAuthenticatedUser();
        if (user.isPresent()) {
            AuthenticatedUserDTO authenticatedUserDTO = user.get();
            Operator operator = new Operator();
            operator.setOperatorId(String.valueOf(authenticatedUserDTO.userId()));
            return operator;
        }
        return null;
    }
}
