module.exports = {
  title: "Yamp",
  tagline: "Yet another message protocol",
  url: "https://yamp.netlify.app",
  baseUrl: "/",
  favicon: "img/favicon.ico",
  organizationName: "procilon",
  projectName: "yamp",
  themeConfig: {
    navbar: {
      title: "Yamp",
      links: [
        {
          to: "docs/overview",
          activeBasePath: "docs",
          label: "Docs",
          position: "left"
        },
        {
          href: "https://github.com/MaxFichtelmann/yamp",
          label: "GitHub",
          position: "right"
        }
      ]
    },
    footer: {
      style: "dark",
      links: [
        {
          title: "Docs",
          items: [
            {
              label: "Overview",
              to: "docs/overview"
            },
            {
              label: "Getting Started",
              to: "docs/getting-started"
            }
          ]
        },
        {
          title: "Community",
          items: [
            {
              label: "Stack Overflow",
              href: "https://stackoverflow.com/questions/tagged/yamp"
            }
          ]
        },
        {
          title: "Social",
          items: [
            {
              label: "GitHub",
              href: "https://github.com/MaxFichtelmann/yamp"
            },
            {
              label: "Twitter",
              href: "https://twitter.com/Fichtelmax"
            }
          ]
        }
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Max Fichtelmann, Built with Docusaurus.`
    }
  },
  presets: [
    [
      "@docusaurus/preset-classic",
      {
        docs: {
          sidebarPath: require.resolve("./sidebars.js")
        },
        theme: {
          customCss: require.resolve("./src/css/custom.css")
        }
      }
    ]
  ]
};
