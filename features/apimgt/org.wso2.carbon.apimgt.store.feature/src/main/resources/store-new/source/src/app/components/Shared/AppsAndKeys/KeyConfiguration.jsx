/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';

import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import { FormattedMessage } from 'react-intl';
import Button from '@material-ui/core/Button';
import ResourceNotFound from '../../Base/Errors/ResourceNotFound';
import Application from '../../../data/Application';

const styles = theme => ({
    FormControl: {
        padding: theme.spacing.unit * 2,
        width: '100%',
    },
    FormControlOdd: {
        padding: theme.spacing.unit * 2,
        width: '100%',
    },
    button: {
        marginLeft: theme.spacing.unit * 1,
    },
    quotaHelp: {
        position: 'relative',
    },
    checkboxWrapper: {
        display: 'flex',
    },
    checkboxWrapperColumn: {
        display: 'flex',
        flexDirection: 'row',
    },
    group: {
        flexDirection: 'row',
    },
});
/**
 *
 *
 * @class KeyConfiguration
 * @extends {React.Component}
 */
class KeyConfiguration extends React.Component {
    state = {

    }

    /**
     *
     *
     * @returns {Component}
     * @memberof KeyConfiguration
     */
    render() {
        const {
            classes, updateKeyRequest, keyRequest, notFound,
        } = this.props;
        const { supportedGrantTypes, callbackUrl } = keyRequest;
        if (notFound) {
            return <ResourceNotFound />;
        }

        let isRefreshChecked = false;
        let isPasswordChecked = false;
        let isImplicitChecked = false;
        let isCodeChecked = false;
        if (supportedGrantTypes) {
            isRefreshChecked = supportedGrantTypes.includes('refresh_token');
            isPasswordChecked = supportedGrantTypes.includes('password');
            isImplicitChecked = supportedGrantTypes.includes('implicit');
            isCodeChecked = supportedGrantTypes.includes('authorization_code');
        }

        return (
            <React.Fragment>
                <FormControl className={classes.FormControl} component='fieldset'>
                    <InputLabel shrink htmlFor='age-label-placeholder' className={classes.quotaHelp}>
                        <FormattedMessage id='grant.types' defaultMessage='Grant Types' />
                    </InputLabel>
                    <div className={classes.checkboxWrapper}>
                        <div className={classes.checkboxWrapperColumn}>
                            <FormControlLabel control={<Checkbox id='refresh_token' checked={isRefreshChecked} onChange={e => updateKeyRequest('grantType', e)} value='refresh_token' />} label='Refresh Token' />
                            <FormControlLabel control={<Checkbox id='password' checked={isPasswordChecked} value='password' onChange={e => updateKeyRequest('grantType', e)} />} label='Password' />
                            <FormControlLabel control={<Checkbox id='implicit' checked={isImplicitChecked} value='implicit' onChange={e => updateKeyRequest('grantType', e)} />} label='Implicit' />
                        </div>
                        <div className={classes.checkboxWrapperColumn}>
                            <FormControlLabel control={<Checkbox id='authorization_code' checked={isCodeChecked} value='authorization_code' onChange={e => updateKeyRequest('grantType', e)} />} label='Code' />
                            <FormControlLabel control={<Checkbox id='client_credentials' checked disabled value='client_credentials' />} label='Client Credential' />
                        </div>
                    </div>
                    <FormHelperText>The application can use the following grant types to generate Access Tokens. Based on the application requirement, you can enable or disable grant types for this application.</FormHelperText>
                </FormControl>

                {
                    <FormControl className={classes.FormControlOdd}>
                        <TextField id='callbackURL' fullWidth onChange={e => updateKeyRequest('callbackUrl', e)} label='Callback URL' placeholder='http://url-to-webapp' className={classes.textField} margin='normal' value={callbackUrl} />
                        <FormHelperText>Callback URL is a redirection URI in the client application which is used by the authorization server to send the client's user-agent (usually web browser) back after granting access.</FormHelperText>
                    </FormControl>
                }
            </React.Fragment>
        );
    }
}

export default withStyles(styles)(KeyConfiguration);
