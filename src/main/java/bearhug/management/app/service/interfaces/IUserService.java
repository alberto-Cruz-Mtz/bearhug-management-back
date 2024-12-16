package bearhug.management.app.service.interfaces;

import bearhug.management.app.presentation.dto.AuthRequest;
import bearhug.management.app.presentation.dto.AuthResponse;

public interface IUserService {

    AuthResponse register(AuthRequest authRequest);

    AuthResponse authenticate(AuthRequest authRequest);
}
