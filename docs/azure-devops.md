# Azure DevOps integration

Azure DevOps is currently the only ticket provider wired into FlowProbe.

The integration supports storing provider configuration and optionally creating a work item after a failed flow.

## Configure Azure DevOps

Run:

```bash
flowprobe configure azure
```

FlowProbe asks for:

```text
Azure DevOps organization
Azure DevOps project
Azure DevOps work item type
Azure DevOps Personal Access Token (PAT)
```

The resulting configuration is stored through `java-keyring` in the operating-system credential store.

FlowProbe does not intentionally persist the PAT in a project-local plain-text configuration file.

## Secret input

When a Java `Console` is available, the PAT is read through `Console.readPassword` so it is not echoed by the terminal.

When no `Console` is attached, FlowProbe currently falls back to standard-input reading.

## Unsupported providers

The CLI validates the provider before provider configuration runs.

For example:

```bash
flowprobe configure jira
```

is rejected as invalid CLI input because the current supported provider is:

```text
azure
```

## Create a work item after a failed flow

Ticket creation is opt-in:

```bash
flowprobe run \
  -f /path/to/flow.yaml \
  --create-impediment
```

The work item is created only when flow execution fails.

FlowProbe builds the work-item description from the failed request context using its reproducible request renderer.

## Sensitive header redaction

Before a rendered cURL request is reused in failure output or ticket context, common sensitive HTTP headers are redacted.

Current redacted header names include:

```text
Authorization
Proxy-Authorization
Cookie
X-API-Key
Api-Key
X-Auth-Token
X-Access-Token
X-Amz-Security-Token
```

This redaction is header-name based. FlowProbe does not currently inspect arbitrary secrets embedded in URLs or request bodies.

## PAT scope

Use the narrowest PAT scope required for work-item creation.

Azure DevOps documents `vso.work_write` as the scope that grants read/create/update access to work items.

Official API documentation:

https://learn.microsoft.com/en-us/rest/api/azure/devops/wit/work-items/create?view=azure-devops-rest-7.1

## Failure behavior

Azure DevOps integration separates provider/API failures from normal flow validation.

A flow can fail without creating a ticket if `--create-impediment` was not supplied.

When ticket creation is requested, FlowProbe reads the stored Azure DevOps configuration and sends the work-item request through the Azure DevOps REST integration.
