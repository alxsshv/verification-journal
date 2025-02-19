package com.alxsshv.journal.service.interfaces;

import com.alxsshv.security.model.User;

import java.io.IOException;
import javax.naming.OperationNotSupportedException;

public interface DigitalSignatureService {

    void setUserStamp(long id, User currentUser) throws OperationNotSupportedException, IOException;
}
