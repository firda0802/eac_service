package com.eac.messages;

import com.eac.entity.Assignment;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RespAssignmentStudent {
   private Assignment assignment;
   private String status;
}
