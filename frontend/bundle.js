/* eslint @typescript-eslint/no-var-requires: 0 */
const Bundler = require('parcel-bundler');
const Path = require('path');
const shelljs = require('shelljs');
const fs = require('fs');

////////////////////////////////
// Fixes the bug 'Ant Table in minified production deployment crashes'
// https://github.com/ant-design/ant-design/issues/22943#issuecomment-614483120
const fileList = ['BodyContext', 'ResizeContext', 'TableContext'];
console.log(__dirname);
const folder = Path.resolve(__dirname, './node_modules/rc-table/es/context');

fileList.forEach((item) => {
    const file = Path.resolve(folder, item + '.js');
    const context = fs.readFileSync(file).toString();

    if (!/twig_obfuscation_/gm.test(context)) {
        fs.writeFileSync(
            file,
            `${context}export function twig_obfuscation_${item}(){};`,
        );
    }
});
////////////////////////////////

// Single entrypoint file location:
const entryFiles = Path.join(__dirname, './src/index.html');
const tsProtoDir = Path.join(__dirname, './src/proto');
const protobufDir = Path.join(__dirname, '../protobuf');
const outDir = Path.join(__dirname, './dist');
const nodeModuleDir = Path.join(__dirname, './node_modules');

process.env.NODE_ENV = 'production';

// Bundler options
const options = {
    outDir: outDir, // The out directory to put the build files in, defaults to dist
    outFile: 'index.html', // The name of the outputFile
    publicUrl: '/app', // The url to serve on, defaults to '/'
    watch: false, // Whether to watch the files and rebuild them on change, defaults to process.env.NODE_ENV !== 'production'
    cache: false, // Enabled or disables caching, defaults to true
    // cacheDir: '.cache', // The directory cache gets put in, defaults to .cache
    contentHash: true, // Disable content hash from being included on the filename
    global: 'moduleName', // Expose modules as UMD under this name, disabled by default
    minify: true, // Minify files, enabled if process.env.NODE_ENV === 'production'
    scopeHoist: false, // Turn on experimental scope hoisting/tree shaking flag, for smaller production bundles
    target: 'browser', // Browser/node/electron, defaults to browser
    bundleNodeModules: true, // By default, package.json dependencies are not included when using 'node' or 'electron' with 'target' option above. Set to true to adds them to the bundle, false by default
    logLevel: 3, // 5 = save everything to a file, 4 = like 3, but with timestamps and additionally log http requests to dev server, 3 = log info, warnings & errors, 2 = log warnings & errors, 1 = log errors
    sourceMaps: false, // Enable or disable sourcemaps, defaults to enabled (minified builds currently always create sourcemaps)hmrHostname: '', // A hostname for hot module reload, default to ''
    detailedReport: true, // Prints a detailed report of the bundles, assets, file sizes and times, defaults to false, reports are only printed if watch is disabled
};

async function bundle() {
    // Initializes a bundler using the entrypoint location and options provided
    const bundler = new Bundler(entryFiles, options);
    bundler.loadPlugins();

    // Run the bundler, this returns the main bundle
    // Use the events if you're using watch mode as this promise will only trigger once and not for every rebuild
    try {
        await bundler.bundle();
    } catch (error) {
        console.error(error);
        process.exit(1);
    }
}

async function generateProto() {
    shelljs.exec(`rm -rf ${tsProtoDir}/**/*.ts`);

    const path = `${protobufDir}/**/*.proto`;
    const protoPath = `--proto_path ${protobufDir}`;
    const plugin = `--plugin=${nodeModuleDir}/.bin/protoc-gen-ts_proto`;
    const tsProtoOut = `--ts_proto_out=${tsProtoDir}`;
    const options = [
        'env=browser',
        'esModuleInterop=true',
        'outputEncodeMethods=false',
        'outputJsonMethods=true',
        'outputClientImpl=false',
        'useOptionals=true',
    ].join(',');
    const tsProtoOpt = '--ts_proto_opt=' + options;
    const command = [
        'protoc',
        path,
        protoPath,
        plugin,
        tsProtoOut,
        tsProtoOpt,
    ].join(' ');

    shelljs.exec(command);
}

async function build() {
    await generateProto();

    if (process.argv.indexOf('--generate-proto-only') !== -1) {
        console.log('Generates proto only.');
        return;
    }
    await bundle();
    console.log('Build completed.');
}

(async function () {
    await build();
})();
