# Maker-Checker Architecture

## Core rules

- The client never assigns the authorizer.
- Authorization is resolved server-side from department ownership, active assignments, delegations, and conflict rules.
- `inputterUserId != authorizerUserId`
- `lastModifiedBy != authorizerUserId`
- Authorized records are immutable; material changes create a new pending version.

## Sequence

```mermaid
sequenceDiagram
    participant M as Inputter
    participant S as Business Service
    participant R as Authorizer Resolver
    participant A as Authorizer
    participant K as Kafka
    participant U as Audit Service

    M->>S: Create or update draft
    M->>S: Submit for authorization
    S->>R: Resolve eligible authorizer
    R-->>S: Department head / valid delegate / escalation target
    S->>K: authorization.requested.v1
    S->>U: immutable audit event
    A->>S: Start review
    A->>S: Approve / Reject / Return
    S->>K: authorization outcome event
    S->>U: immutable audit event
```
