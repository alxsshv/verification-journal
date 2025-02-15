package com.alxsshv.journal.service.interfaces;

import com.alxsshv.security.model.User;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;


public interface DigitalSignatureService {
    void setUserStamp(long id, User currentUser) throws OperationNotSupportedException, IOException;
}
