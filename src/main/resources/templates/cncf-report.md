## Non-approved dependencies

| Dependency | Description | License(s) | Website | Source repository |
| ---------- | ----------- | ---------- | ------- | ----------------- |
{#each dependency in report.uniqueDependencies}
{#if !dependency.approvedByCncf}
| [{#if dependency.group}{dependency.group}:{/if}{dependency.name}]({dependency.packageUrl}) | {dependency.description} | {#each license in dependency.allDeclaredLicenses}[{license.licenseId}]({license.reference}) {/each} | [{dependency.webUrl}]({dependency.webUrl}) | [{dependency.sourceUrl}]({dependency.sourceUrl}) |
{/if}
{/each}
