package org.javers.api;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author pawel szymczyk
 */
@RestController
@RequestMapping("/javers")
public class JaversApiController {

    private final JaversQueryService javersQueryService;

    public JaversApiController(JaversQueryService javersQueryService) {
        this.javersQueryService = javersQueryService;
    }

    @GetMapping(path = "/v1/snapshots", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public SnapshotsResponse snapshots(@RequestParam String instanceId, @RequestParam String className) {
        List<CdoSnapshot> snapshots = javersQueryService.findSnapshots(instanceId, className);
        return new SnapshotsResponse(snapshots);
    }
}
