package com.apisec.apilab.catalog;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CurriculumCatalog {

    private final List<LearningModule> modules = List.of(
            new LearningModule(
                    "foundations",
                    "M1",
                    "API Security Foundations",
                    "Beginner",
                    25,
                    "How APIs differ from web pages, where trust boundaries sit, and how this lab maps to OWASP API Security Top 10.",
                    List.of(
                            "Describe authentication vs authorization in an API",
                            "Identify common API assets: tokens, object IDs, and schemas",
                            "Explain why a learning lab uses a secure student portal plus later vulnerable APIs"
                    ),
                    List.of(
                            new Lesson("What an API actually exposes", "Endpoints, verbs, JSON bodies, and identifiers that often leak authorization decisions."),
                            new Lesson("The student portal vs the lab APIs", "This app’s login is meant to be solid. Future labs will sit beside it as deliberately weak services."),
                            new Lesson("Threat model in one page", "Who the attacker is, what they can send, and what a flag will represent later.")
                    ),
                    List.of(
                            "Never trust the client to pick object IDs or roles.",
                            "Tokens are credentials — treat them like passwords.",
                            "Secure comparison views come after you can name the failure."
                    )
            ),
            new LearningModule(
                    "bola",
                    "M2",
                    "Broken Object Level Authorization",
                    "Beginner",
                    30,
                    "The most common API bug: changing an ID in the URL or body and reading someone else’s record.",
                    List.of(
                            "Recognize IDOR / BOLA in REST paths like /orders/{id}",
                            "Explain why hiding IDs is not an access control",
                            "List a server-side check that would stop it"
                    ),
                    List.of(
                            new Lesson("Object IDs are not secrets", "Sequential integers and UUIDs both fail if the server never asks “does this user own this row?”"),
                            new Lesson("Horizontal privilege", "Same role, different owner — the classic student vs student case."),
                            new Lesson("Secure pattern", "Load the resource, compare owner to the authenticated principal, then 404 or 403.")
                    ),
                    List.of(
                            "Authorization belongs on every object, not only at login.",
                            "A 200 with another user’s JSON is the vulnerability.",
                            "Later labs will let you swap IDs; this module is the theory."
                    )
            ),
            new LearningModule(
                    "broken-auth",
                    "M3",
                    "Broken Authentication",
                    "Beginner",
                    30,
                    "Weak login, predictable tokens, and missing expiration — contrasted with the JWT flow you already use here.",
                    List.of(
                            "Contrast this portal’s hashed passwords and JWT expiry with weak lab patterns",
                            "Name risks of tokens in localStorage vs HttpOnly cookies",
                            "Describe why generic login errors matter"
                    ),
                    List.of(
                            new Lesson("Your Day 1 login (the secure side)", "BCrypt passwords, JWT with identity + role + expiry, generic invalid-credential messages."),
                            new Lesson("What labs will break", "Unsigned tokens, role in the payload trusted blindly, no expiry, verbose “email not found”."),
                            new Lesson("Session vs token APIs", "When to rotate, revoke, and keep tokens out of URLs.")
                    ),
                    List.of(
                            "Authentication proves who you are; it does not prove which objects you may see.",
                            "Never put secrets in JWT payloads you cannot verify.",
                            "This portal is the “secure” comparison for later auth labs."
                    )
            ),
            new LearningModule(
                    "mass-assignment",
                    "M4",
                    "Excessive Data & Mass Assignment",
                    "Intermediate",
                    25,
                    "APIs that return too many fields, or bind extra JSON properties such as role=ADMIN into an update.",
                    List.of(
                            "Spot oversharing in a user JSON response",
                            "Explain mass assignment on PATCH /profile",
                            "Name DTO allow-lists as the fix"
                    ),
                    List.of(
                            new Lesson("Oversharing", "Internal flags, other users’ emails, or password hashes in a “full” object."),
                            new Lesson("Mass assignment", "Client sends {\"role\":\"ADMIN\"} and the binder writes it because the entity is bound directly."),
                            new Lesson("Secure comparison", "This app’s profile DTO only exposes name, email, role, createdAt — and role is not client-writable.")
                    ),
                    List.of(
                            "Return a DTO, not the entity.",
                            "Bind only fields the current user is allowed to change.",
                            "Flagging labs will come later; the pattern is already visible on /profile.")
            ),
            new LearningModule(
                    "misconfig",
                    "M5",
                    "Security Misconfiguration",
                    "Beginner",
                    20,
                    "Open CORS, stack traces, default credentials, and verbose 500s — operational failures that still count as API issues.",
                    List.of(
                            "List three misconfigurations that leak data without a clever payload",
                            "Explain why error bodies should stay generic for clients",
                            "Relate this to the centralized exception handler in the lab"
                    ),
                    List.of(
                            new Lesson("Default and leftover admin", "Unchanged secrets, debug consoles, and sample users left in production."),
                            new Lesson("Error handling", "This app’s API errors use a shared handler so login does not reveal whether an email exists."),
                            new Lesson("What we will add later", "OpenAPI, Docker, and a vulnerable sibling API with the opposite settings.")
                    ),
                    List.of(
                            "Misconfiguration is often easier than crafting an exploit.",
                            "Fail closed: deny by default, hide internals.",
                            "Compare this portal’s quiet errors with future noisy lab APIs.")
            )
    );

    private final List<LabPreview> labs = List.of(
            new LabPreview(
                    "shop-idor",
                    "Shop Orders — object IDs",
                    "Easy",
                    "bola",
                    "A future orders API will let you change /orders/101 to /orders/102. This preview documents the goal; live flagging is not enabled yet.",
                    List.of(
                            "Find an order identifier in a JSON list",
                            "Predict what an ID swap would prove",
                            "Write the server-side ownership check you would expect"
                    ),
                    "Preview"
            ),
            new LabPreview(
                    "jwt-playground",
                    "Token playground",
                    "Easy",
                    "broken-auth",
                    "Contrast the portal JWT (signed, expiring, role as a claim) with a lab token that will later trust alg=none or a role field.",
                    List.of(
                            "Inspect your current ACCESS_TOKEN cookie conceptually (do not share it)",
                            "List three JWT anti-patterns",
                            "Map each anti-pattern to a secure control this app already uses"
                    ),
                    "Preview"
            ),
            new LabPreview(
                    "profile-bind",
                    "Profile binder",
                    "Medium",
                    "mass-assignment",
                    "A later PATCH lab will accept extra fields. For now, compare that idea with GET /api/profile, which returns a locked-down DTO.",
                    List.of(
                            "Call GET /api/profile with your session",
                            "Note which fields are absent (password, id internals)",
                            "Design an allow-list for a future update endpoint"
                    ),
                    "Preview"
            )
    );

    private final List<ApiEndpoint> explorer = List.of(
            new ApiEndpoint("POST", "/api/auth/register", "Create a STUDENT account. Password is hashed; duplicate emails return 409.", "Public",
                    "{\"token\":\"eyJ...\",\"tokenType\":\"Bearer\",\"name\":\"Ada\",\"email\":\"ada@example.com\",\"role\":\"STUDENT\"}"),
            new ApiEndpoint("POST", "/api/auth/login", "Authenticate and receive a JWT. Invalid credentials always use the same message.", "Public",
                    "{\"token\":\"eyJ...\",\"role\":\"STUDENT\"}"),
            new ApiEndpoint("GET", "/api/profile", "Current user profile from the JWT principal. Protected.", "JWT cookie or Bearer",
                    "{\"name\":\"Ada\",\"email\":\"ada@example.com\",\"role\":\"STUDENT\",\"createdAt\":\"2026-09-21T10:00:00Z\"}"),
            new ApiEndpoint("GET", "/api/catalog/modules", "Read-only curriculum list for this learning portal.", "JWT",
                    "[{\"slug\":\"foundations\",\"title\":\"API Security Foundations\"}]")
    );

    private final List<QuizQuestion> assessment = List.of(
            new QuizQuestion(
                    "q1",
                    "A student changes GET /orders/5 to GET /orders/6 and receives another user’s order. What failed?",
                    List.of("Encryption at rest", "Object-level authorization", "TLS version", "Password hashing"),
                    1,
                    "That is BOLA / IDOR: the server did not check ownership of the object."
            ),
            new QuizQuestion(
                    "q2",
                    "This student portal stores passwords with BCrypt and issues a signed JWT. That primarily addresses:",
                    List.of("Broken object level authorization", "Broken authentication storage and session quality", "Mass assignment", "SSRF"),
                    1,
                    "Hashing and a proper token are authentication controls. They do not, by themselves, authorize object access."
            ),
            new QuizQuestion(
                    "q3",
                    "A PATCH /users/me endpoint binds the JSON body onto a User entity, so {\"role\":\"ADMIN\"} succeeds. This is:",
                    List.of("Rate limiting failure", "Mass assignment", "Open redirect", "CSRF on a GET"),
                    1,
                    "Extra properties were trusted because the API did not use an allow-listed DTO."
            ),
            new QuizQuestion(
                    "q4",
                    "Login returns “email not found” vs “wrong password”. Why is that a problem?",
                    List.of("It helps UX only, no security impact", "It lets attackers enumerate accounts", "It breaks JWT signatures", "It disables HTTPS"),
                    1,
                    "Verbose auth errors leak whether an account exists. This lab uses a single invalid-credential message."
            ),
            new QuizQuestion(
                    "q5",
                    "Which response design matches the secure profile API in this app?",
                    List.of("Return the JPA entity including the password hash", "Return a DTO with name, email, role, createdAt", "Return every column “for convenience”", "Put the password in the JWT payload"),
                    1,
                    "GET /api/profile uses a dedicated DTO and never returns the password."
            )
    );

    public List<LearningModule> modules() {
        return modules;
    }

    public Optional<LearningModule> module(String slug) {
        return modules.stream().filter(m -> m.slug().equals(slug)).findFirst();
    }

    public List<LabPreview> labs() {
        return labs;
    }

    public Optional<LabPreview> lab(String slug) {
        return labs.stream().filter(l -> l.slug().equals(slug)).findFirst();
    }

    public List<ApiEndpoint> explorer() {
        return explorer;
    }

    public List<QuizQuestion> assessment() {
        return assessment;
    }
}
