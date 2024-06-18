## License IDs

{cncf.unapprovedLicenseIds}

## License Names

{cncf.unapprovedLicenseNames}

## Component details

| Dependency | Description | License(s) | Website | Source repository |
| ---------- | ----------- | ---------- | ------- | ----------------- |
{#each dependency in cncf.uniqueDependencies}
{#if !dependency.approvedByCncf}
| [{#if dependency.group}{dependency.group}:{/if}{dependency.name}]({dependency.packageUrl}) | {dependency.description} | {#each license in dependency.allDeclaredLicenses}{#if !license_isFirst} OR {/if}[{license.licenseId}]({license.reference}){/each} | [{dependency.webUrl}]({dependency.webUrl}) | [{dependency.sourceUrl}]({dependency.sourceUrl}) |
{/if}
{/each}
