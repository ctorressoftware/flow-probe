package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;

public record AzureDevOpsWorkItemResponse(
        int id,
        int rev,
        Fields fields,
        Map<String, String> multilineFieldsFormat,
        Links links,
        String url
) {

    public record Fields(
            @JsonProperty("System.AreaPath")
            String areaPath,

            @JsonProperty("System.TeamProject")
            String teamProject,

            @JsonProperty("System.IterationPath")
            String iterationPath,

            @JsonProperty("System.WorkItemType")
            String workItemType,

            @JsonProperty("System.State")
            String state,

            @JsonProperty("System.Reason")
            String reason,

            @JsonProperty("System.CreatedDate")
            Instant createdDate,

            @JsonProperty("System.CreatedBy")
            Identity createdBy,

            @JsonProperty("System.ChangedDate")
            Instant changedDate,

            @JsonProperty("System.ChangedBy")
            Identity changedBy,

            @JsonProperty("System.CommentCount")
            int commentCount,

            @JsonProperty("System.Title")
            String title,

            @JsonProperty("System.BoardColumn")
            String boardColumn,

            @JsonProperty("System.BoardColumnDone")
            boolean boardColumnDone,

            @JsonProperty("Microsoft.VSTS.Common.StateChangeDate")
            Instant stateChangeDate,

            @JsonProperty("Microsoft.VSTS.Common.Priority")
            int priority,

            @JsonProperty("WEF_A18EB701C64644A0945E223C1F5B23FE_Kanban.Column")
            String kanbanColumn,

            @JsonProperty("WEF_A18EB701C64644A0945E223C1F5B23FE_Kanban.Column.Done")
            boolean kanbanColumnDone,

            @JsonProperty("System.Description")
            String description
    ) {}

    public record Identity(
            String displayName,
            String url,

            @JsonProperty("_links")
            IdentityLinks links,

            String id,
            String uniqueName,
            String imageUrl,
            String descriptor
    ) {}

    public record IdentityLinks(
            Avatar avatar
    ) {}

    public record Avatar(
            String href
    ) {}

    public record Links(
            Link self,
            Link workItemUpdates,
            Link workItemRevisions,
            Link workItemComments,
            Link html,
            Link workItemType,
            Link fields
    ) {}

    public record Link(
            String href
    ) {}
}