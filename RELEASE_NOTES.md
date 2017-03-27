# Release notes for Warp10 Quantum

## Version 2.4.0

- We have changed our building pipeline to  use `polymer-cli` version 0.18. Some properties in the `polymer.json` have changed and two build targets (`bundled` and `unbundled`) have been declared. The build won't work anymore with older versions of `polymer-cli`
- The default backend for the *Choose other backend* option have been fixed. In HTTP it will always point to `http://127.0.0.1:8080/api/v0`, in HTTPS it will empty. The label has been update to remind the required schema (`http[s]://[host]:[port]/api/v0`)
- Missing imports of QuantumModule were added to some files on Warp10 Quantum elements