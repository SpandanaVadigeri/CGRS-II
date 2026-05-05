package Backend.CGRSBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityDto {
    private Long id;
    private String name;
    private String department;
    private String email; // from linked User
}
