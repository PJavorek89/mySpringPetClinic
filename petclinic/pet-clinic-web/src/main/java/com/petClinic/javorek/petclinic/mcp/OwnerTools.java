package com.petClinic.javorek.petclinic.mcp;

import com.petClinic.javorek.petclinic.dto.OwnerDto;
import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.services.OwnerService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerTools {

    private final OwnerService ownerService;
    private final ClinicMapper clinicMapper;

    public OwnerTools(OwnerService ownerService,
                      ClinicMapper clinicMapper) {
        this.ownerService = ownerService;
        this.clinicMapper = clinicMapper;
    }

    @McpTool(name = "find_owners", description = """
        Search owners by the beginning of their last name (case-insensitive). \
        Use the exact spelling with diacritics, e.g. 'Novák'. \
        Returns owners with their pets, including owner ids and pet ids. \
        Use it to get ownerId and petId before calling get_owner, add_pet or record_visit. \
        If several owners match, ask the user which one.""")
    public List<OwnerDto> findOwners(
            @McpToolParam(description = "Beginning of the owner's last name, e.g. 'Nov' finds 'Novák'. Leave empty to list all owners.",
                    required = false)
            String lastName) {
        return ownerService.search(lastName).stream().map(clinicMapper::toDto).toList();
    }
}
